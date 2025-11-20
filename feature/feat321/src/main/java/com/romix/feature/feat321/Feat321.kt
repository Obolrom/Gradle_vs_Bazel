package com.romix.feature.feat321

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat321Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat321UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat321FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat321UserSummary
)

data class Feat321UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat321NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat321Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat321Config = Feat321Config()
) {

    fun loadSnapshot(userId: Long): Feat321NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat321NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat321UserSummary {
        return Feat321UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat321FeedItem> {
        val result = java.util.ArrayList<Feat321FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat321FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat321UiMapper {

    fun mapToUi(model: List<Feat321FeedItem>): Feat321UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat321UiModel(
            header = UiText("Feat321 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat321UiModel =
        Feat321UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat321UiModel =
        Feat321UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat321UiModel =
        Feat321UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat321Service(
    private val repository: Feat321Repository,
    private val uiMapper: Feat321UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat321UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat321UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat321UserItem1(val user: CoreUser, val label: String)
data class Feat321UserItem2(val user: CoreUser, val label: String)
data class Feat321UserItem3(val user: CoreUser, val label: String)
data class Feat321UserItem4(val user: CoreUser, val label: String)
data class Feat321UserItem5(val user: CoreUser, val label: String)
data class Feat321UserItem6(val user: CoreUser, val label: String)
data class Feat321UserItem7(val user: CoreUser, val label: String)
data class Feat321UserItem8(val user: CoreUser, val label: String)
data class Feat321UserItem9(val user: CoreUser, val label: String)
data class Feat321UserItem10(val user: CoreUser, val label: String)

data class Feat321StateBlock1(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock2(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock3(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock4(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock5(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock6(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock7(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock8(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock9(val state: Feat321UiModel, val checksum: Int)
data class Feat321StateBlock10(val state: Feat321UiModel, val checksum: Int)

fun buildFeat321UserItem(user: CoreUser, index: Int): Feat321UserItem1 {
    return Feat321UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat321StateBlock(model: Feat321UiModel): Feat321StateBlock1 {
    return Feat321StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat321UserSummary> {
    val list = java.util.ArrayList<Feat321UserSummary>(users.size)
    for (user in users) {
        list += Feat321UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat321UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat321UiModel {
    val summaries = (0 until count).map {
        Feat321UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat321UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat321UiModel> {
    val models = java.util.ArrayList<Feat321UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat321AnalyticsEvent1(val name: String, val value: String)
data class Feat321AnalyticsEvent2(val name: String, val value: String)
data class Feat321AnalyticsEvent3(val name: String, val value: String)
data class Feat321AnalyticsEvent4(val name: String, val value: String)
data class Feat321AnalyticsEvent5(val name: String, val value: String)
data class Feat321AnalyticsEvent6(val name: String, val value: String)
data class Feat321AnalyticsEvent7(val name: String, val value: String)
data class Feat321AnalyticsEvent8(val name: String, val value: String)
data class Feat321AnalyticsEvent9(val name: String, val value: String)
data class Feat321AnalyticsEvent10(val name: String, val value: String)

fun logFeat321Event1(event: Feat321AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat321Event2(event: Feat321AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat321Event3(event: Feat321AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat321Event4(event: Feat321AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat321Event5(event: Feat321AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat321Event6(event: Feat321AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat321Event7(event: Feat321AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat321Event8(event: Feat321AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat321Event9(event: Feat321AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat321Event10(event: Feat321AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat321Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat321Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat321(u: CoreUser): Feat321Projection1 =
    Feat321Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat321Projection1> {
    val list = java.util.ArrayList<Feat321Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat321(u)
    }
    return list
}
