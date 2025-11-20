package com.romix.feature.feat15

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat15Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat15UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat15FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat15UserSummary
)

data class Feat15UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat15NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat15Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat15Config = Feat15Config()
) {

    fun loadSnapshot(userId: Long): Feat15NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat15NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat15UserSummary {
        return Feat15UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat15FeedItem> {
        val result = java.util.ArrayList<Feat15FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat15FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat15UiMapper {

    fun mapToUi(model: List<Feat15FeedItem>): Feat15UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat15UiModel(
            header = UiText("Feat15 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat15UiModel =
        Feat15UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat15UiModel =
        Feat15UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat15UiModel =
        Feat15UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat15Service(
    private val repository: Feat15Repository,
    private val uiMapper: Feat15UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat15UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat15UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat15UserItem1(val user: CoreUser, val label: String)
data class Feat15UserItem2(val user: CoreUser, val label: String)
data class Feat15UserItem3(val user: CoreUser, val label: String)
data class Feat15UserItem4(val user: CoreUser, val label: String)
data class Feat15UserItem5(val user: CoreUser, val label: String)
data class Feat15UserItem6(val user: CoreUser, val label: String)
data class Feat15UserItem7(val user: CoreUser, val label: String)
data class Feat15UserItem8(val user: CoreUser, val label: String)
data class Feat15UserItem9(val user: CoreUser, val label: String)
data class Feat15UserItem10(val user: CoreUser, val label: String)

data class Feat15StateBlock1(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock2(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock3(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock4(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock5(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock6(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock7(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock8(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock9(val state: Feat15UiModel, val checksum: Int)
data class Feat15StateBlock10(val state: Feat15UiModel, val checksum: Int)

fun buildFeat15UserItem(user: CoreUser, index: Int): Feat15UserItem1 {
    return Feat15UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat15StateBlock(model: Feat15UiModel): Feat15StateBlock1 {
    return Feat15StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat15UserSummary> {
    val list = java.util.ArrayList<Feat15UserSummary>(users.size)
    for (user in users) {
        list += Feat15UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat15UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat15UiModel {
    val summaries = (0 until count).map {
        Feat15UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat15UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat15UiModel> {
    val models = java.util.ArrayList<Feat15UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat15AnalyticsEvent1(val name: String, val value: String)
data class Feat15AnalyticsEvent2(val name: String, val value: String)
data class Feat15AnalyticsEvent3(val name: String, val value: String)
data class Feat15AnalyticsEvent4(val name: String, val value: String)
data class Feat15AnalyticsEvent5(val name: String, val value: String)
data class Feat15AnalyticsEvent6(val name: String, val value: String)
data class Feat15AnalyticsEvent7(val name: String, val value: String)
data class Feat15AnalyticsEvent8(val name: String, val value: String)
data class Feat15AnalyticsEvent9(val name: String, val value: String)
data class Feat15AnalyticsEvent10(val name: String, val value: String)

fun logFeat15Event1(event: Feat15AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat15Event2(event: Feat15AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat15Event3(event: Feat15AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat15Event4(event: Feat15AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat15Event5(event: Feat15AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat15Event6(event: Feat15AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat15Event7(event: Feat15AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat15Event8(event: Feat15AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat15Event9(event: Feat15AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat15Event10(event: Feat15AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat15Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat15Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat15(u: CoreUser): Feat15Projection1 =
    Feat15Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat15Projection1> {
    val list = java.util.ArrayList<Feat15Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat15(u)
    }
    return list
}
