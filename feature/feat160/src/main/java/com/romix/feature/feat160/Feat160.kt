package com.romix.feature.feat160

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat160Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat160UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat160FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat160UserSummary
)

data class Feat160UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat160NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat160Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat160Config = Feat160Config()
) {

    fun loadSnapshot(userId: Long): Feat160NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat160NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat160UserSummary {
        return Feat160UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat160FeedItem> {
        val result = java.util.ArrayList<Feat160FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat160FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat160UiMapper {

    fun mapToUi(model: List<Feat160FeedItem>): Feat160UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat160UiModel(
            header = UiText("Feat160 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat160UiModel =
        Feat160UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat160UiModel =
        Feat160UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat160UiModel =
        Feat160UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat160Service(
    private val repository: Feat160Repository,
    private val uiMapper: Feat160UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat160UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat160UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat160UserItem1(val user: CoreUser, val label: String)
data class Feat160UserItem2(val user: CoreUser, val label: String)
data class Feat160UserItem3(val user: CoreUser, val label: String)
data class Feat160UserItem4(val user: CoreUser, val label: String)
data class Feat160UserItem5(val user: CoreUser, val label: String)
data class Feat160UserItem6(val user: CoreUser, val label: String)
data class Feat160UserItem7(val user: CoreUser, val label: String)
data class Feat160UserItem8(val user: CoreUser, val label: String)
data class Feat160UserItem9(val user: CoreUser, val label: String)
data class Feat160UserItem10(val user: CoreUser, val label: String)

data class Feat160StateBlock1(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock2(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock3(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock4(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock5(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock6(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock7(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock8(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock9(val state: Feat160UiModel, val checksum: Int)
data class Feat160StateBlock10(val state: Feat160UiModel, val checksum: Int)

fun buildFeat160UserItem(user: CoreUser, index: Int): Feat160UserItem1 {
    return Feat160UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat160StateBlock(model: Feat160UiModel): Feat160StateBlock1 {
    return Feat160StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat160UserSummary> {
    val list = java.util.ArrayList<Feat160UserSummary>(users.size)
    for (user in users) {
        list += Feat160UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat160UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat160UiModel {
    val summaries = (0 until count).map {
        Feat160UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat160UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat160UiModel> {
    val models = java.util.ArrayList<Feat160UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat160AnalyticsEvent1(val name: String, val value: String)
data class Feat160AnalyticsEvent2(val name: String, val value: String)
data class Feat160AnalyticsEvent3(val name: String, val value: String)
data class Feat160AnalyticsEvent4(val name: String, val value: String)
data class Feat160AnalyticsEvent5(val name: String, val value: String)
data class Feat160AnalyticsEvent6(val name: String, val value: String)
data class Feat160AnalyticsEvent7(val name: String, val value: String)
data class Feat160AnalyticsEvent8(val name: String, val value: String)
data class Feat160AnalyticsEvent9(val name: String, val value: String)
data class Feat160AnalyticsEvent10(val name: String, val value: String)

fun logFeat160Event1(event: Feat160AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat160Event2(event: Feat160AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat160Event3(event: Feat160AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat160Event4(event: Feat160AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat160Event5(event: Feat160AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat160Event6(event: Feat160AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat160Event7(event: Feat160AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat160Event8(event: Feat160AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat160Event9(event: Feat160AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat160Event10(event: Feat160AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat160Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat160Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat160(u: CoreUser): Feat160Projection1 =
    Feat160Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat160Projection1> {
    val list = java.util.ArrayList<Feat160Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat160(u)
    }
    return list
}
