package com.romix.feature.feat682

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat682Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat682UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat682FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat682UserSummary
)

data class Feat682UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat682NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat682Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat682Config = Feat682Config()
) {

    fun loadSnapshot(userId: Long): Feat682NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat682NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat682UserSummary {
        return Feat682UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat682FeedItem> {
        val result = java.util.ArrayList<Feat682FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat682FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat682UiMapper {

    fun mapToUi(model: List<Feat682FeedItem>): Feat682UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat682UiModel(
            header = UiText("Feat682 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat682UiModel =
        Feat682UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat682UiModel =
        Feat682UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat682UiModel =
        Feat682UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat682Service(
    private val repository: Feat682Repository,
    private val uiMapper: Feat682UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat682UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat682UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat682UserItem1(val user: CoreUser, val label: String)
data class Feat682UserItem2(val user: CoreUser, val label: String)
data class Feat682UserItem3(val user: CoreUser, val label: String)
data class Feat682UserItem4(val user: CoreUser, val label: String)
data class Feat682UserItem5(val user: CoreUser, val label: String)
data class Feat682UserItem6(val user: CoreUser, val label: String)
data class Feat682UserItem7(val user: CoreUser, val label: String)
data class Feat682UserItem8(val user: CoreUser, val label: String)
data class Feat682UserItem9(val user: CoreUser, val label: String)
data class Feat682UserItem10(val user: CoreUser, val label: String)

data class Feat682StateBlock1(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock2(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock3(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock4(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock5(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock6(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock7(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock8(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock9(val state: Feat682UiModel, val checksum: Int)
data class Feat682StateBlock10(val state: Feat682UiModel, val checksum: Int)

fun buildFeat682UserItem(user: CoreUser, index: Int): Feat682UserItem1 {
    return Feat682UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat682StateBlock(model: Feat682UiModel): Feat682StateBlock1 {
    return Feat682StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat682UserSummary> {
    val list = java.util.ArrayList<Feat682UserSummary>(users.size)
    for (user in users) {
        list += Feat682UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat682UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat682UiModel {
    val summaries = (0 until count).map {
        Feat682UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat682UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat682UiModel> {
    val models = java.util.ArrayList<Feat682UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat682AnalyticsEvent1(val name: String, val value: String)
data class Feat682AnalyticsEvent2(val name: String, val value: String)
data class Feat682AnalyticsEvent3(val name: String, val value: String)
data class Feat682AnalyticsEvent4(val name: String, val value: String)
data class Feat682AnalyticsEvent5(val name: String, val value: String)
data class Feat682AnalyticsEvent6(val name: String, val value: String)
data class Feat682AnalyticsEvent7(val name: String, val value: String)
data class Feat682AnalyticsEvent8(val name: String, val value: String)
data class Feat682AnalyticsEvent9(val name: String, val value: String)
data class Feat682AnalyticsEvent10(val name: String, val value: String)

fun logFeat682Event1(event: Feat682AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat682Event2(event: Feat682AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat682Event3(event: Feat682AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat682Event4(event: Feat682AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat682Event5(event: Feat682AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat682Event6(event: Feat682AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat682Event7(event: Feat682AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat682Event8(event: Feat682AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat682Event9(event: Feat682AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat682Event10(event: Feat682AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat682Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat682Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat682(u: CoreUser): Feat682Projection1 =
    Feat682Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat682Projection1> {
    val list = java.util.ArrayList<Feat682Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat682(u)
    }
    return list
}
