package com.romix.feature.feat115

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat115Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat115UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat115FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat115UserSummary
)

data class Feat115UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat115NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat115Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat115Config = Feat115Config()
) {

    fun loadSnapshot(userId: Long): Feat115NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat115NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat115UserSummary {
        return Feat115UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat115FeedItem> {
        val result = java.util.ArrayList<Feat115FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat115FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat115UiMapper {

    fun mapToUi(model: List<Feat115FeedItem>): Feat115UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat115UiModel(
            header = UiText("Feat115 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat115UiModel =
        Feat115UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat115UiModel =
        Feat115UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat115UiModel =
        Feat115UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat115Service(
    private val repository: Feat115Repository,
    private val uiMapper: Feat115UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat115UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat115UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat115UserItem1(val user: CoreUser, val label: String)
data class Feat115UserItem2(val user: CoreUser, val label: String)
data class Feat115UserItem3(val user: CoreUser, val label: String)
data class Feat115UserItem4(val user: CoreUser, val label: String)
data class Feat115UserItem5(val user: CoreUser, val label: String)
data class Feat115UserItem6(val user: CoreUser, val label: String)
data class Feat115UserItem7(val user: CoreUser, val label: String)
data class Feat115UserItem8(val user: CoreUser, val label: String)
data class Feat115UserItem9(val user: CoreUser, val label: String)
data class Feat115UserItem10(val user: CoreUser, val label: String)

data class Feat115StateBlock1(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock2(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock3(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock4(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock5(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock6(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock7(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock8(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock9(val state: Feat115UiModel, val checksum: Int)
data class Feat115StateBlock10(val state: Feat115UiModel, val checksum: Int)

fun buildFeat115UserItem(user: CoreUser, index: Int): Feat115UserItem1 {
    return Feat115UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat115StateBlock(model: Feat115UiModel): Feat115StateBlock1 {
    return Feat115StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat115UserSummary> {
    val list = java.util.ArrayList<Feat115UserSummary>(users.size)
    for (user in users) {
        list += Feat115UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat115UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat115UiModel {
    val summaries = (0 until count).map {
        Feat115UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat115UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat115UiModel> {
    val models = java.util.ArrayList<Feat115UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat115AnalyticsEvent1(val name: String, val value: String)
data class Feat115AnalyticsEvent2(val name: String, val value: String)
data class Feat115AnalyticsEvent3(val name: String, val value: String)
data class Feat115AnalyticsEvent4(val name: String, val value: String)
data class Feat115AnalyticsEvent5(val name: String, val value: String)
data class Feat115AnalyticsEvent6(val name: String, val value: String)
data class Feat115AnalyticsEvent7(val name: String, val value: String)
data class Feat115AnalyticsEvent8(val name: String, val value: String)
data class Feat115AnalyticsEvent9(val name: String, val value: String)
data class Feat115AnalyticsEvent10(val name: String, val value: String)

fun logFeat115Event1(event: Feat115AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat115Event2(event: Feat115AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat115Event3(event: Feat115AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat115Event4(event: Feat115AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat115Event5(event: Feat115AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat115Event6(event: Feat115AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat115Event7(event: Feat115AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat115Event8(event: Feat115AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat115Event9(event: Feat115AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat115Event10(event: Feat115AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat115Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat115Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat115(u: CoreUser): Feat115Projection1 =
    Feat115Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat115Projection1> {
    val list = java.util.ArrayList<Feat115Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat115(u)
    }
    return list
}
