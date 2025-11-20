package com.romix.feature.feat377

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat377Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat377UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat377FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat377UserSummary
)

data class Feat377UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat377NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat377Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat377Config = Feat377Config()
) {

    fun loadSnapshot(userId: Long): Feat377NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat377NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat377UserSummary {
        return Feat377UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat377FeedItem> {
        val result = java.util.ArrayList<Feat377FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat377FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat377UiMapper {

    fun mapToUi(model: List<Feat377FeedItem>): Feat377UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat377UiModel(
            header = UiText("Feat377 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat377UiModel =
        Feat377UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat377UiModel =
        Feat377UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat377UiModel =
        Feat377UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat377Service(
    private val repository: Feat377Repository,
    private val uiMapper: Feat377UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat377UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat377UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat377UserItem1(val user: CoreUser, val label: String)
data class Feat377UserItem2(val user: CoreUser, val label: String)
data class Feat377UserItem3(val user: CoreUser, val label: String)
data class Feat377UserItem4(val user: CoreUser, val label: String)
data class Feat377UserItem5(val user: CoreUser, val label: String)
data class Feat377UserItem6(val user: CoreUser, val label: String)
data class Feat377UserItem7(val user: CoreUser, val label: String)
data class Feat377UserItem8(val user: CoreUser, val label: String)
data class Feat377UserItem9(val user: CoreUser, val label: String)
data class Feat377UserItem10(val user: CoreUser, val label: String)

data class Feat377StateBlock1(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock2(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock3(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock4(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock5(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock6(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock7(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock8(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock9(val state: Feat377UiModel, val checksum: Int)
data class Feat377StateBlock10(val state: Feat377UiModel, val checksum: Int)

fun buildFeat377UserItem(user: CoreUser, index: Int): Feat377UserItem1 {
    return Feat377UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat377StateBlock(model: Feat377UiModel): Feat377StateBlock1 {
    return Feat377StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat377UserSummary> {
    val list = java.util.ArrayList<Feat377UserSummary>(users.size)
    for (user in users) {
        list += Feat377UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat377UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat377UiModel {
    val summaries = (0 until count).map {
        Feat377UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat377UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat377UiModel> {
    val models = java.util.ArrayList<Feat377UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat377AnalyticsEvent1(val name: String, val value: String)
data class Feat377AnalyticsEvent2(val name: String, val value: String)
data class Feat377AnalyticsEvent3(val name: String, val value: String)
data class Feat377AnalyticsEvent4(val name: String, val value: String)
data class Feat377AnalyticsEvent5(val name: String, val value: String)
data class Feat377AnalyticsEvent6(val name: String, val value: String)
data class Feat377AnalyticsEvent7(val name: String, val value: String)
data class Feat377AnalyticsEvent8(val name: String, val value: String)
data class Feat377AnalyticsEvent9(val name: String, val value: String)
data class Feat377AnalyticsEvent10(val name: String, val value: String)

fun logFeat377Event1(event: Feat377AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat377Event2(event: Feat377AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat377Event3(event: Feat377AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat377Event4(event: Feat377AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat377Event5(event: Feat377AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat377Event6(event: Feat377AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat377Event7(event: Feat377AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat377Event8(event: Feat377AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat377Event9(event: Feat377AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat377Event10(event: Feat377AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat377Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat377Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat377(u: CoreUser): Feat377Projection1 =
    Feat377Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat377Projection1> {
    val list = java.util.ArrayList<Feat377Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat377(u)
    }
    return list
}
