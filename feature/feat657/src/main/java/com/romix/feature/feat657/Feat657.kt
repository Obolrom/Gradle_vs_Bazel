package com.romix.feature.feat657

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat657Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat657UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat657FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat657UserSummary
)

data class Feat657UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat657NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat657Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat657Config = Feat657Config()
) {

    fun loadSnapshot(userId: Long): Feat657NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat657NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat657UserSummary {
        return Feat657UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat657FeedItem> {
        val result = java.util.ArrayList<Feat657FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat657FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat657UiMapper {

    fun mapToUi(model: List<Feat657FeedItem>): Feat657UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat657UiModel(
            header = UiText("Feat657 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat657UiModel =
        Feat657UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat657UiModel =
        Feat657UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat657UiModel =
        Feat657UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat657Service(
    private val repository: Feat657Repository,
    private val uiMapper: Feat657UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat657UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat657UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat657UserItem1(val user: CoreUser, val label: String)
data class Feat657UserItem2(val user: CoreUser, val label: String)
data class Feat657UserItem3(val user: CoreUser, val label: String)
data class Feat657UserItem4(val user: CoreUser, val label: String)
data class Feat657UserItem5(val user: CoreUser, val label: String)
data class Feat657UserItem6(val user: CoreUser, val label: String)
data class Feat657UserItem7(val user: CoreUser, val label: String)
data class Feat657UserItem8(val user: CoreUser, val label: String)
data class Feat657UserItem9(val user: CoreUser, val label: String)
data class Feat657UserItem10(val user: CoreUser, val label: String)

data class Feat657StateBlock1(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock2(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock3(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock4(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock5(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock6(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock7(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock8(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock9(val state: Feat657UiModel, val checksum: Int)
data class Feat657StateBlock10(val state: Feat657UiModel, val checksum: Int)

fun buildFeat657UserItem(user: CoreUser, index: Int): Feat657UserItem1 {
    return Feat657UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat657StateBlock(model: Feat657UiModel): Feat657StateBlock1 {
    return Feat657StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat657UserSummary> {
    val list = java.util.ArrayList<Feat657UserSummary>(users.size)
    for (user in users) {
        list += Feat657UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat657UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat657UiModel {
    val summaries = (0 until count).map {
        Feat657UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat657UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat657UiModel> {
    val models = java.util.ArrayList<Feat657UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat657AnalyticsEvent1(val name: String, val value: String)
data class Feat657AnalyticsEvent2(val name: String, val value: String)
data class Feat657AnalyticsEvent3(val name: String, val value: String)
data class Feat657AnalyticsEvent4(val name: String, val value: String)
data class Feat657AnalyticsEvent5(val name: String, val value: String)
data class Feat657AnalyticsEvent6(val name: String, val value: String)
data class Feat657AnalyticsEvent7(val name: String, val value: String)
data class Feat657AnalyticsEvent8(val name: String, val value: String)
data class Feat657AnalyticsEvent9(val name: String, val value: String)
data class Feat657AnalyticsEvent10(val name: String, val value: String)

fun logFeat657Event1(event: Feat657AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat657Event2(event: Feat657AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat657Event3(event: Feat657AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat657Event4(event: Feat657AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat657Event5(event: Feat657AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat657Event6(event: Feat657AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat657Event7(event: Feat657AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat657Event8(event: Feat657AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat657Event9(event: Feat657AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat657Event10(event: Feat657AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat657Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat657Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat657(u: CoreUser): Feat657Projection1 =
    Feat657Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat657Projection1> {
    val list = java.util.ArrayList<Feat657Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat657(u)
    }
    return list
}
